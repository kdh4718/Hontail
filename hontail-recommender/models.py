from sqlalchemy import Column, Integer, String, Text, ForeignKey
from sqlalchemy.orm import relationship
from database import Base

class Cocktail(Base):
    __tablename__ = "cocktail"

    cocktail_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), nullable=False)
    maker_nickname = Column(String(50), nullable=False)
    cocktail_name = Column(String(100), nullable=False)
    cocktail_description = Column(Text, nullable=True)
    alcohol_content = Column(Integer, nullable=False)
    image_url = Column(String(255), nullable=True)
    is_custom = Column(Integer, default=0)
    base_spirit = Column(String(50), nullable=True)

class Ingredient(Base):
    __tablename__ = "ingredients"

    ingredient_id = Column(Integer, primary_key=True, index=True)
    ingredient_name = Column(String(100), nullable=False, unique=True)
    ingredient_name_kor = Column(String(100), nullable=False)
    ingredient_category = Column(String(100), nullable=True)
    ingredient_category_kor = Column(String(100), nullable=True)
    ingredient_type = Column(String(50), nullable=True)
    ingredient_alcohol_content = Column(Integer, nullable=True)
    ingredient_image = Column(Text, nullable=True)

class CocktailIngredient(Base):
    __tablename__ = "cocktail_ingredients"

    cocktail_ingredient_id = Column(Integer, primary_key=True, index=True)
    cocktail_id = Column(Integer, ForeignKey("cocktail.cocktail_id"), nullable=False)
    ingredient_id = Column(Integer, ForeignKey("ingredients.ingredient_id"), nullable=False)
    ingredient_quantity = Column(String(50), nullable=True)

class Like(Base):
    __tablename__ = "likes"

    like_id = Column(Integer, primary_key=True, index=True)
    cocktail_id = Column(Integer, ForeignKey("cocktail.cocktail_id"), nullable=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), nullable=True)
