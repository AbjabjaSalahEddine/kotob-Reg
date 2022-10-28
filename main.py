import uvicorn
from fastapi import FastAPI
from routes import router
from config import PORT
from methods import addMe
app=FastAPI()

@app.on_event("startup")
async def startup_event():
    print(addMe())

@app.get("/")
def root():
    print("Hello World!!")
    return {"msg":"Hello World!!"}


app.include_router(router, tags=["book"])

if __name__ == "__main__":

    uvicorn.run("main:app", host="127.0.0.1", port=PORT , reload=True)