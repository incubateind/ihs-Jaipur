import tkinter as tk
from tkinter import*
import requests
from tkinter import filedialog
from PIL import Image, ImageTk
import pytesseract
from selenium import webdriver
import time
hi = 700
wi = 1200

def UploadAction(event=None):
    filename = filedialog.askopenfilename()
# print('Selected:', filename)
    global text_source
    a = (filename)
    print (a)
    from PIL import Image
    source = Image.open(a)
    #display(source)
    pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files (x86)\Tesseract-OCR\tesseract.exe"
    text_source = pytesseract.image_to_string(source)
    print(text_source)
    text_source = text_source.strip()
    text_source = text_source.replace("\n", " ")
    text_source

def UploadAction1(event=None):
    filename = filedialog.askopenfilename()
#     print('Selected:', filename)
    a = (filename)
    from PIL import Image
    global text_destination
    destination = Image.open(a)
    #display(destination)
    pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files (x86)\Tesseract-OCR\tesseract.exe"
    text_destination = pytesseract.image_to_string(destination)
    print(text_destination)
    text_destination = text_destination.strip()
    text_destination = text_destination.replace("\n", " ")
    text_destination

def UploadAction2(event=None):
    #browser = webdriver.Chrome("C:\\Users\\Monu\\AppData\\Local\\Programs\\Python\\Python37\\Lib\\site-packages\\selenium\\webdriver\\webdriver.exe")
    browser=webdriver.Chrome("C:\\Users\\Monu\\Desktop\\Jaipur Hackathon\\chromedriver.exe")
    browser.get('https://www.google.com/maps')
    time.sleep(2)
    searchbtn=browser.find_element_by_id('searchbox-directions')
    time.sleep(2)
    searchbtn.click()
    time.sleep(2)
    searchbtn2=browser.find_element_by_xpath('//div[@id="directions-searchbox-0"]//div[@class="gstl_51 sbib_a"]//div[@id="sb_ifc51"]//input[@class="tactile-searchbox-input"]')
    searchbtn2.send_keys(text_source)
    time.sleep(4)
    searchbtn3=browser.find_element_by_xpath('//div[@id="directions-searchbox-1"]//div[@class="gstl_52 sbib_a"]//div[@id="sb_ifc52"]//input[@class="tactile-searchbox-input"]')
    searchbtn3.click()
    searchbtn3.send_keys(text_destination)
    time.sleep(4)
    searchbtn4=browser.find_element_by_xpath('//div[@id="directions-searchbox-1"]//button[@class="searchbox-searchbutton"]')
    searchbtn4.click()
    #type(text_source)

m = tk.Tk()
m.title("Easy Go")

canvas = tk.Canvas(m, height=hi, width=wi)
canvas.pack()

background_image = tk.PhotoImage(file="gm.png")
backgorund_label = tk.Label(m, image=background_image)
backgorund_label.place(relwidth=1, relheight=1)

z = tk.Label(m, text="EASY GO", bg="white", font=('Helvetica', 24, 'bold italic'))
z.place(relx = 0.40, rely = 0.05, relwidth=0.17, relheight =0.1)

x = tk.Label(m, text="Upload the source image", bg="cyan", font=('Times Header', 12))
x.place(relx = 0.13, rely = 0.47, relwidth=0.17, relheight =0.08)


y = tk.Label(m, text="Upload the destination image", bg="cyan", font=('Times Header', 12))
y.place(relx = 0.13, rely = 0.6, relwidth=0.17, relheight =0.08)

frame = tk.Frame(m, bg = '#131B88', bd=5)
frame.place(relx=0.40, rely=0.48, relwidth=0.05, relheight=0.055, anchor='n')

button1 = tk.Button(frame, text='Source', command=UploadAction)
button1.place(relx = 0.8, rely = 0.2, relwidth=30, relheight =1 )
button1.config(height=100,width=100)
button1.pack()

lower_frame = tk.Frame(m, bg='#131B88', bd=5 )
lower_frame.place(relx=0.40, rely=0.615, relwidth=0.065, relheight=0.055, anchor='n')

button2 = tk.Button(lower_frame, text='Destination', command=UploadAction1)
button2.place(relx = 0.2, rely = 0.2, relwidth=10, relheight =10 )
button2.config(height=100,width=100)
button2.pack()

go_frame = tk.Frame(m, bg='#7A83F3', bd=5 )
go_frame.place(relx=0.55, rely=0.545, relwidth=0.06, relheight=0.055, anchor='n')

button3 = tk.Button(go_frame, text='GO', command=UploadAction2)
button3.place(relx = 0.20, rely = 0.2, relwidth=400, height =400)
button3.config(height=200, width=200)
button3.pack()

m.mainloop()



