import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy.orm import Session
from models import Cocktail, Like, CocktailIngredient, Ingredient
from joblib import Memory

# 캐싱 설정
memory = Memory(location="cache_dir", verbose=0)

def get_user_favorite_ingredients(user_id: int, db: Session):
    """ 사용자가 좋아요한 칵테일의 재료 목록 반환 """
    liked_cocktails = db.query(Like).filter(Like.user_id == user_id).all()
    cocktail_ids = [like.cocktail_id for like in liked_cocktails]

    ingredients = (
        db.query(Ingredient.ingredient_name)
        .join(CocktailIngredient, CocktailIngredient.ingredient_id == Ingredient.ingredient_id)
        .filter(CocktailIngredient.cocktail_id.in_(cocktail_ids))
        .distinct()
        .all()
    )
    return [ingredient[0] for ingredient in ingredients]

@memory.cache
def fit_tfidf(data):
    """ TF-IDF 벡터화 및 모델 학습 """
    if data.empty:
        raise ValueError("TF-IDF 벡터화할 데이터가 없습니다.")
    
    tfidf_vectorizer = TfidfVectorizer()
    return tfidf_vectorizer.fit_transform(data), tfidf_vectorizer

def get_recommendations(user_id: int, db: Session, top_n: int = 5):
    """ 사용자가 좋아요한 칵테일의 재료를 기반으로 추천 """
    user_ingredients = get_user_favorite_ingredients(user_id, db)
    if not user_ingredients:
        raise ValueError("사용자가 좋아요한 재료가 없습니다.")

    cocktails = db.query(Cocktail).all()
    if not cocktails:
        return []

    data = []
    for cocktail in cocktails:
        ingredients = (
            db.query(Ingredient.ingredient_name)
            .join(CocktailIngredient, CocktailIngredient.ingredient_id == Ingredient.ingredient_id)
            .filter(CocktailIngredient.cocktail_id == cocktail.cocktail_id)
            .all()
        )
        ingredient_list = [ingredient[0] for ingredient in ingredients]
        data.append({"id": cocktail.cocktail_id, "name": cocktail.cocktail_name, "ingredients": " ".join(ingredient_list)})

    df = pd.DataFrame(data)

    # TF-IDF 벡터화 (캐싱 적용)
    tfidf_matrix, tfidf_vectorizer = fit_tfidf(df["ingredients"])
    user_vector = tfidf_vectorizer.transform([" ".join(user_ingredients)])

    # 유사도 계산
    cosine_sim = cosine_similarity(user_vector, tfidf_matrix).flatten()
    df["similarity"] = cosine_sim
    recommendations = df.sort_values(by="similarity", ascending=False).head(top_n)
    
    return recommendations.to_dict(orient="records")
