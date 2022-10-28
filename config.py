from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

PORT = 8082

DATABASE_URL = 'postgresql://postgres:12344321@localhost:5432/kotob_scannedboks_service'



engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush = False, bind=engine)
Base = declarative_base()