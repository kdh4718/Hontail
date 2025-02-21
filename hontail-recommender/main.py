from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from database import get_db
from recommender import get_recommendations

app = FastAPI()

@app.get("/recommend/{user_id}")
def recommend(user_id: int, top_n: int = 5, db: Session = Depends(get_db)):
    """ 사용자의 좋아요한 칵테일을 기반으로 추천 """
    recommendations = get_recommendations(user_id, db, top_n)
    return {"recommended_cocktails": recommendations}