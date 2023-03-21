import cv2 
import numpy as np
#from matplotlib import pyplot as plt
#from PIL import Image
import base64
#cifrado
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.fernet import Fernet





def main (data):
    decode_data = base64.b64decode(data)
    np_data=np.fromstring(decode_data,np.uint8)
    #np_data=np.frombuffer(decode_data,np.uint8)
    img=cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
    x = 0 
    y = 0
    w = 1088 
    h = 1088
    n=18
    bits = []
    counter = 0
    texto_final = ''
    texto=[]
    hei, wid = img.shape[:2]
    enter = "1010101010101010"
    dict_letters_zero = {"1001101010101001": { "letter" : "A", "count": 0}, "1001011010101001": { "letter" : "a", "count": 0}, "1001011010100110": { "letter" : "b", "count": 0}, "1001101010100110": { "letter" : "B", "count": 0},"1001011010100101": { "letter" : "c", "count": 0}, "1001101010100101": { "letter" : "C", "count": 0} , "1001011010011010": { "letter" : "d", "count": 0}, "1001101010011010": { "letter" : "D", "count": 0}, "1001011010011001": { "letter" : "e", "count": 0}, "1001101010011001": { "letter" : "E", "count": 0}, "1001011010010110": { "letter" : "f", "count": 0}, "1001101010010110": { "letter" : "F", "count": 0}, "1001011010010101": { "letter" : "g", "count": 0}, "1001101010010101": { "letter" : "G", "count": 0}, "1001011001101010": { "letter" : "h", "count": 0}, "1001101001101010": { "letter" : "H", "count": 0}, "1001011001101001": { "letter" : "i", "count": 0}, "1001101001101001": { "letter" : "I", "count": 0}, "1001011001100110": { "letter" : "j", "count": 0}, "1001101001100110": { "letter" : "J", "count": 0}, "1001011001100101": { "letter" : "k", "count": 0}, "1001101001100101": { "letter" : "K", "count": 0}, "1001011001011010": { "letter" : "l", "count": 0}, "1001101001011010": { "letter" : "L", "count": 0}, "1001011001011001": { "letter" : "m", "count": 0}, "1001101001011001": { "letter" : "M", "count": 0}, "1001011001010110": { "letter" : "n", "count": 0}, "1001101001010110": { "letter" : "N", "count": 0}, "1001011001010101": { "letter" : "o", "count": 0}, "1001101001010101": { "letter" : "O", "count": 0}, "1001010110101010": { "letter" : "p", "count": 0}, "1001100110101010": { "letter" : "P", "count": 0}, "1001010110101001": { "letter" : "q", "count": 0}, "1001100110101001": { "letter" : "Q", "count": 0}, "1001010110100110": { "letter" : "r", "count": 0}, "1001100110100110": { "letter" : "R", "count": 0}, "1001010101101010": { "letter" : "x", "count": 0}, "1010010110101010": { "letter" : "0", "count": 0}, "1010010110101001": { "letter" : "1", "count": 0}, "1010010110100110": { "letter" : "2", "count": 0}, "1010010110100101": { "letter" : "3", "count": 0}, "1010010110011010": { "letter" : "4", "count": 0}, "1010010110011001": { "letter" : "5", "count": 0}, "1010010110010110": { "letter" : "6", "count": 0}, "1010010110010101": { "letter" : "7", "count": 0}, "1010010101101010": { "letter" : "8", "count": 0}, "1010010101101001": { "letter" : "9", "count": 0},"1001010110100101":{"letter":"s","count":0}}
    dict_letters = {"1010101010101010":{ "letter" : "enter", "count": 0},"1001101010101001": { "letter" : "A", "count": 0}, "1001011010101001": { "letter" : "a", "count": 0}, "1001011010100110": { "letter" : "b", "count": 0}, "1001101010100110": { "letter" : "B", "count": 0},"1001011010100101": { "letter" : "c", "count": 0}, "1001101010100101": { "letter" : "C", "count": 0},"1001011010011010": { "letter" : "d", "count": 0},"1001101010011010": { "letter" : "D", "count": 0}, "1001011010011001": { "letter" : "e", "count": 0}, "1001101010011001": { "letter" : "E", "count": 0}, "1001011010010110": { "letter" : "f", "count": 0}, "1001101010010110": { "letter" : "F", "count": 0}, "1001011010010101": { "letter" : "g", "count": 0}, "1001101010010101": { "letter" : "G", "count": 0}, "1001011001101010": { "letter" : "h", "count": 0}, "1001101001101010": { "letter" : "H", "count": 0},"1001011001101001": { "letter" : "i", "count": 0}, "1001101001101001": { "letter" : "I", "count": 0}, "1001011001100110": { "letter" : "j", "count": 0}, "1001101001100110": { "letter" : "J", "count": 0}, "1001011001100101": { "letter" : "k", "count": 0}, "1001101001100101": { "letter" : "K", "count": 0}, "1001011001011010": { "letter" : "l", "count": 0}, "1001101001011010": { "letter" : "L", "count": 0}, "1001011001011001": { "letter" : "m", "count": 0}, "1001101001011001": { "letter" : "M", "count": 0}, "1001011001010110": { "letter" : "n", "count": 0}, "1001101001010110": { "letter" : "N", "count": 0}, "1001011001010101": { "letter" : "o", "count": 0}, "1001101001010101": { "letter" : "O", "count": 0}, "1001010110101010": { "letter" : "p", "count": 0}, "1001100110101010": { "letter" : "P", "count": 0}, "1001010110101001": { "letter" : "q", "count": 0}, "1001100110101001": { "letter" : "Q", "count": 0}, "1001010110100110": { "letter" : "r", "count": 0}, "1001100110100110": { "letter" : "R", "count": 0}, "1001010101101010": { "letter" : "x", "count": 0}, "1010010110101010": { "letter" : "0", "count": 0}, "1010010110101001": { "letter" : "1", "count": 0}, "1010010110100110": { "letter" : "2", "count": 0}, "1010010110100101": { "letter" : "3", "count": 0}, "1010010110011010": { "letter" : "4", "count": 0}, "1010010110011001": { "letter" : "5", "count": 0}, "1010010110010110": { "letter" : "6", "count": 0}, "1010010110010101": { "letter" : "7", "count": 0}, "1010010101101010": { "letter" : "8", "count": 0}, "1010010101101001": { "letter" : "9", "count": 0},"1001010110100101":{"letter":"s","count":0}}
    imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    imgBlur = cv2.GaussianBlur(imgGray, (5,5),5)
    ret,th = cv2.threshold(imgBlur,100,255,cv2.THRESH_BINARY)
    datos=[]
    w_bits=0  #400    
    corte_blanco=0
    corte_negro=0
    y_image = 0
    im=np.array(th)    
    h=len(im)
    for i in range(1,len(im[0]),1): 
    #print(im[int(600)][i]>=250)
        if im[int(100)][i] >= 250:
            if(im[int(150)][i] >= 250):
                im3=th[100:150,x:x+w]
                break
            elif(im[int(50)][i] >= 250):
                im3=th[50:100,x:x+w]
                break
        elif (im[int(200)][i] >= 250):
            if(im[int(250)][i] >= 250):
                im3=th[200:250,x:x+w]
                break
            elif(im[int(150)][i] >= 250):
                im3=th[150:200,x:x+w]
                break
        elif (im[int(300)][i] >= 250):
            if(im[int(350)][i] >= 250):
                im3=th[300:350,x:x+w]
                break
            elif(im[int(250)][i] >= 250):
                im3=th[250:300,x:x+w]
                break
        elif (im[int(400)][i] >= 250):
            if(im[int(450)][i] >= 250):
                im3=th[400:450,x:x+w]
                break
            elif(im[int(350)][i] >= 250):
                im3=th[350:400,x:x+w]
                break
        elif (im[int(500)][i] >= 250):
            if(im[int(550)][i] >= 250):
                im3=th[500:550,x:x+w]
                break
            elif(im[int(450)][i] >= 250):
                im3=th[450:500,x:x+w]
                break
        elif (im[int(600)][i] >= 250):
            if(im[int(650)][i] >= 250):
                im3=th[600:650,x:x+w]
                break
            elif(im[int(550)][i] >= 250):
                im3=th[550:600,x:x+w]
                break
        elif (im[int(700)][i] >= 250):
            if(im[int(750)][i] >= 250):
                im3=th[700:750,x:x+w]
                break
            elif(im[int(650)][i] >= 250):
                im3=th[650:700,x:x+w]
                break       
        elif (im[int(800)][i] >= 250):
            if(im[int(850)][i] >= 250):
                im3=th[800:850,x:x+w]
                break
            elif(im[int(750)][i] >= 250):
                im3=th[750:800,x:x+w]
                break 
        elif (im[int(900)][i] >= 250):
            if(im[int(950)][i] >= 250):
                im3=th[900:950,x:x+w]
                break
            elif(im[int(850)][i] >= 250):
                im3=th[850:900,x:x+w]
                break
        elif (im[int(1000)][i] >= 250):
            if(im[int(1050)][i] >= 250):
                im3=th[1000:1050,x:x+w]
                break
            elif(im[int(950)][i] >= 250):
                im3=th[950:1000,x:x+w]
                break
    im=np.array(im3)    
    h=len(im3)

    for i in range(1,len(im[0]),1):        
        avg_color = im[int(h/2)][i]
        if (avg_color >= 250 ):
            corte_blanco += i 
            #print(corte_blanco)
            break

    for j in range(corte_blanco,len(im[0])-corte_blanco,1):
        avg_color = im[int(h/2)][j]
        if (avg_color <= 10 ):
            corte_negro += j 
            #print(corte_negro)
            break

    for k in range(len(im[0])-1,0,-1):
        avg_color = im[int(h/2)][k]
        if (avg_color >= 250 ):
            w_bits=k-corte_negro
            #print(w_bits)
            #print("ancho")
            break


    img2= im[y_image:y_image+h, corte_negro:corte_negro+w_bits]
    for j in range(0,len(img2[0]),1):
        avg_color = img2[int(h/2)][j]
        if (avg_color >= 250):
            datos.append("1")
        elif(avg_color <= 10):
            datos.append("0")

    strBin_com = "".join(datos)
    new_strBin_com = strBin_com.replace('10',',')
    new_strBin_com1 = new_strBin_com.replace('01',',')
    bin_separado = new_strBin_com1.split(",")
    binarios_final=[]


    listaBits=[]
    countCero=0
    countOne=0
    lista=[]
    for x in bin_separado:
        if x[0] == "0":
            listaBits.append(len(x))
            if len(x)>1 and len(x)<10:
                lista.append("0")
            elif len(x)>10 and len(x)<20:
                lista.append("00") 
        elif x[0] == "1":
            listaBits.append(len(x))
            if len(x)>5 and len(x)<15:
                lista.append("1")
            elif len(x)>15 and len(x)<25:
                lista.append("11")
            elif len(x)>25:
                lista.append("1111")
    palabra = ""
    for x in lista:
        palabra = palabra + x
    #print(palabra)
    palabra="1"+palabra

    letra=""
    for i in range(len(palabra)):

        for clave in dict_letters:
            #print(clave)
            if (i + 16)<=len(palabra): 
                #print(palabra[i:i+16])
                if palabra[i:i+16] == clave:
                    letra=letra+dict_letters[clave]["letter"]
                    #print(dict_letters[clave]["letter"])
    
    return ""+str(letra)

def main2 (data):

    data=data.replace("null","")
    #print(dato)
    dato=data
    inicio=0
    final=0
    lista=[]
    for i in range(len(dato)):
        if((i+5)<len(dato)):
            if dato[i:i+5]=="enter":
                if(dato[i+5:i+5+5]=="enter"):
                    dato=dato.replace("enterenter","enter")
    
    for i in range(len(dato)):
        if((i+5)<len(dato)):
            if dato[i:i+5]=="enter":
                if(dato[i+5:i+5+5]=="enter"):
                    dato=dato.replace("enterenter","enter")


    for i in range(len(dato)):
        if((i+5)<len(dato)):
            if dato[i:i+5]=="enter":
                if(dato[i+6:i+6+5]=="enter"):
                    dato=dato.replace("enter"+dato[i+5]+"enter","enter")

    for i in range(len(dato)):
        if((i+5)<len(dato)):
            if dato[i:i+5]=="enter":
                if(dato[i+6:i+6+5]=="enter"):
                    dato=dato.replace("enter"+dato[i+5]+"enter","enter")
            
    for i in range(len(dato)):
        if((i+5)<len(dato)):
            if dato[i:i+5]=="enter":
                inicio=i
                break
    datoP=dato[inicio+5:len(dato)]
    dato=dato[inicio:len(dato)]
    #print(datoP)
    #print(dato)
    for i in range(len(datoP)):
        if((i+5)<len(datoP)):
            if datoP[i:i+5]=="enter":
                final=i
                #print(final)
                break


    dato=dato[5:final+5]
    #print(dato)
    for i in range(len(dato)):
        if((i+1)<len(dato)):
            if(dato[i]==dato[i+1]):
                dato=dato.replace(dato[i]+dato[i+1],dato[i])

    #print(dato)
    for i in range(len(dato)):
        if(dato[i] not in lista):
            lista.append(dato[i])


    #print(dato)
    #print(lista)
    palabraF=""
    for i in lista:
        palabraF=palabraF+i
    #print(palabraF)


    return ""+palabraF

def main3(data):
    lista= data.split("######")
    ks=lista[0]
    cpr=lista[1]
    cw=lista[2]
    cpr="-----BEGIN PRIVATE KEY-----\n"+cpr+"\n-----END PRIVATE KEY-----"
    cpr=bytes(cpr,'utf-8')
    plaintext =bytes(ks, 'utf-8')
    private_key = serialization.load_pem_private_key(
        cpr,
        password=None,
        backend=default_backend()
    )
    decrypted = private_key.decrypt(base64.b64decode(plaintext),padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),algorithm=hashes.SHA256(),label=None))

    stokeKey=str(decrypted,'utf-8')
    print(stokeKey)
    key=bytes(stokeKey,'utf-8')
    print(key)
    t=Fernet(key)
    texto=bytes(cw,'utf-8')
    
    return ""+str(t.decrypt(texto),'utf-8')