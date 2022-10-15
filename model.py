from typing import Any
from sqlalchemy import  Column, Integer, String
from config import Base




class ScannedBook(Base):
    __tablename__ ="scannedbook"

    id = Column(Integer, primary_key=True)
    txtFilePath = Column(String)
    language = Column(String)

    def __getattribute__(self, __name: str) -> Any:
        return super().__getattribute__(__name)

