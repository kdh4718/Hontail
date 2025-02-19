import random
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy.orm import Session
from sqlalchemy import func
from models import Cocktail, Like, CocktailIngredient, Ingredient
from joblib import Memory

# 캐싱 설정 (이미지 캐싱 방지)
memory = Memory(location="cache_dir", verbose=0)

def get_user_favorite_ingredients(user_id: int, db: Session):
    """ 사용자가 좋아요한 칵테일의 재료 목록 반환 """
    liked_cocktails = db.query(Like).filter(Like.user_id == user_id).all()
    cocktail_ids = [like.cocktail_id for like in liked_cocktails if like.cocktail_id is not None]

    if not cocktail_ids:
        return [], []

    ingredients = (
        db.query(Ingredient.ingredient_name)
        .join(CocktailIngredient, CocktailIngredient.ingredient_id == Ingredient.ingredient_id)
        .filter(CocktailIngredient.cocktail_id.in_(cocktail_ids))
        .distinct()
        .all()
    )
    return [ingredient[0] for ingredient in ingredients], cocktail_ids

@memory.cache
def fit_tfidf(data):
    """ TF-IDF 벡터화 및 모델 학습 """
    if data.empty:
        raise ValueError("TF-IDF 벡터화할 데이터가 없습니다.")
    
    tfidf_vectorizer = TfidfVectorizer()
    return tfidf_vectorizer.fit_transform(data), tfidf_vectorizer

def get_recommendations(user_id: int, db: Session, top_n: int = 5):
    """ 사용자가 좋아요한 칵테일의 재료를 기반으로 추천 (랜덤 1개의 cocktailId만 반환) """
    user_ingredients, liked_cocktail_ids = get_user_favorite_ingredients(user_id, db)
    if not user_ingredients:
        return None  # 추천할 칵테일이 없는 경우 None 반환

    # 모든 칵테일을 조회 (사용자가 좋아요한 칵테일 제외)
    cocktails = (
        db.query(
            Cocktail.cocktail_id,
            Cocktail.cocktail_name,
            Cocktail.image_url,
            Cocktail.alcohol_content,
            Cocktail.base_spirit,
            func.count(CocktailIngredient.ingredient_id).label("ingredient_count"),
            func.count(Like.like_id).label("like_cnt")
        )
        .outerjoin(CocktailIngredient, Cocktail.cocktail_id == CocktailIngredient.cocktail_id)
        .outerjoin(Like, Cocktail.cocktail_id == Like.cocktail_id)
        .filter(~Cocktail.cocktail_id.in_(liked_cocktail_ids))  # 사용자가 좋아요한 칵테일 제외
        .group_by(Cocktail.cocktail_id)
        .all()
    )

    if not cocktails:
        return None  # 추천할 칵테일이 없는 경우 None 반환

    # 칵테일 정보를 DataFrame으로 변환
    data = []
    for cocktail in cocktails:
        ingredients = (
            db.query(Ingredient.ingredient_name)
            .join(CocktailIngredient, CocktailIngredient.ingredient_id == Ingredient.ingredient_id)
            .filter(CocktailIngredient.cocktail_id == cocktail.cocktail_id)
            .all()
        )
        ingredient_list = [ingredient[0] for ingredient in ingredients]
        data.append({
            "id": cocktail.cocktail_id,
            "ingredients": " ".join(ingredient_list)
        })

    if not data:
        return None

    df = pd.DataFrame(data)

    # TF-IDF 벡터화 (캐싱 적용)
    tfidf_matrix, tfidf_vectorizer = fit_tfidf(df["ingredients"])
    user_vector = tfidf_vectorizer.transform([" ".join(user_ingredients)])

    # 유사도 계산
    cosine_sim = cosine_similarity(user_vector, tfidf_matrix).flatten()
    df["similarity"] = cosine_sim

    # 유사도가 높은 상위 N개 추천
    recommendations = df.sort_values(by="similarity", ascending=False).head(top_n)

    if recommendations.empty:
        return None

    # 랜덤으로 1개의 cocktail_id 선택 후 반환
    return int(random.choice(recommendations["id"].tolist()))
