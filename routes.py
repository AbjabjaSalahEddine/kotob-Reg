import threading,aiofiles,os
from fastapi import Form, File, UploadFile,APIRouter,Depends
from config import SessionLocal
from sqlalchemy.orm import Session
from methods import pdftotext,search
from schema import Response
from model import ScannedBook

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/scannedbooks")
async def get_books(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    _books = db.query(ScannedBook).offset(skip).limit(limit).all()
    return Response(status="Ok", code="200", message="Success fetch all data", result=_books)

@router.post("/scannedbooks")
async def creatscannedbook(file: UploadFile  = File(), id: int = Form(...), language: str = Form(...), db: Session = Depends(get_db)):
    async with aiofiles.open("pdfs/"+str(id)+"-"+file.filename, 'wb') as out_file:
        while content := await file.read(1024):  # async read chunk
            await out_file.write(content)  # async write chunk
    
    t1=threading.Thread(target=pdftotext,args=(file.filename,id,language,db))
    t1.start()

    return Response(status="Ok", code="200", message="Processing ...", result={
        "expected textfile path ":"bookstexts/"+str(id)+'-'+file.filename.replace(".pdf",".txt")
        })

@router.get("/search/")
async def findbooks(q:str="",lang:str="eng", db: Session = Depends(get_db)):
    _books = db.query(ScannedBook).all()
    results= search(q,lang,_books)
    return Response(status="Ok", code="200", message="Success fetch all data", result=results)      

@router.delete("/scannedbooks/{id}")
def update(id:int, db: Session = Depends(get_db)):
    book = db.query(ScannedBook).filter(ScannedBook.id == id).first()
    os.remove(book.__getattribute__("txtFilePath"))
    os.remove("./pdfs/"+str(id)+"-"+book.__getattribute__("txtFilePath").split("-")[1].replace(".txt",".pdf"))
    db.delete(book)
    db.commit()
    return Response(status="Ok", code="200", message="Success delete data").dict(exclude_none=True)



    