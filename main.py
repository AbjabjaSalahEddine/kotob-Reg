from fastapi import FastAPI
from routes import router
from model import ScannedBook

app=FastAPI()


@app.get("/")
def root():
    print("Hello World!!")
    return {"msg":"Hello World!!"}


app.include_router(router, tags=["book"])