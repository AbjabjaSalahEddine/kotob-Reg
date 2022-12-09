
from time import sleep
from sqlalchemy.orm import Session
from config import PORT
import requests
import json
import pytesseract
from Levenshtein import distance
import platform
from pdf2image import convert_from_path
from IPython.display import  display,Image
from model import ScannedBook

languages={"Arabic":"ara","Engilsh":"eng","French":"fra","Spanish":"spa"}

def pdftotext(filename,id,lang,db):
    print("Start")
    #read the pdf file into a list of images ; images
    PDF_file="./pdfs/"+str(id)+'-'+filename

    if platform.system() == "Windows":
        images = convert_from_path(
            PDF_file, 500, poppler_path=r"C:/Users/pcc/Desktop/Logiciels/poppler-0.68.0/bin"
        )
    else:
        images = convert_from_path(PDF_file, 500)

    #use tesseract tool which is already installed on the machine
    pytesseract.pytesseract.tesseract_cmd = r'D:\Logiciels\Tesseract-OCR\tesseract.exe'

    #iterate in images reading them using tesseract and writing in the text file
    with open("./bookstexts/"+str(id)+'-'+filename.replace(".pdf",".txt"), "w" , encoding="utf-8") as output_file:
        text=""
        for image in images:
            text+= pytesseract.image_to_string (image, lang=languages[lang])+"\n"
            print(text)
        
        output_file.write(text)
    
    _book = ScannedBook(id=id,txtFilePath="bookstexts/"+str(id)+'-'+filename.replace(".pdf",".txt"),language=lang)
    db.add(_book)
    db.commit()
    db.refresh(_book)
    print("Done")
    return 0



def search(query,lang,books):
    print(type(books).__name__)
    d={}
    mindistance=len(query.split(" "))*3

    for b in books:
        language=languages[b.__getattribute__("language")]
        if language==lang:
            # Read txt file
            f=open(b.__getattribute__("txtFilePath"),'r',encoding="utf8")
            text=f.read().replace("\n"," ").replace('\r',' ').replace('  ',' ')
            s=len(query)
            m=s
            for i in range(len(text)-s):
                m=min(m,distance(query.upper(), text[i:i+s].upper()))
            print(b.__getattribute__("txtFilePath"),"--->",m)
            # Adding the book to result if distance >= mindistance
            if m<=mindistance:
                d[b]=m

    # Sort results according to distance
    results=list(dict(sorted(d.items(), key=lambda item: item[1])).keys())


    return results

def addMe():
    url = "http://127.0.0.1:8080/service-discovery/"

    payload = json.dumps({
    "name": "ScannedBooks_MS",
    "port": str(PORT)
    })
    headers = {
    'Authorization': 'Secret_Key',
    'Content-Type': 'application/json'
    }
    try:
        response = requests.request("POST", url, headers=headers, data=payload)
        response=response.text
    except:
        print("the service getaway is down probably!")
        response="the service getaway is down probably!"
        
    
    return response