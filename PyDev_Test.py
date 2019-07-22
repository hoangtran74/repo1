#FILE PRE HEADER STRIPPER AND CONTENT CHECK
#default variables
#!/usr/bin/env python 
import xml.dom.minidom
from xml.dom.minidom import Node
 
def getXMLfile(file): 
    doc = xml.dom.minidom.parse(file)
    mapping = []
    _EXT = []
    for node in doc.getElementsByTagName("format"):
        L = node.getElementsByTagName("ext")
        for node2 in L:
            ext = ""
            for node3 in node2.childNodes:
                if node3.nodeType == Node.TEXT_NODE:
                    ext += node3.data
            _EXT.append(ext)
    print _EXT  
    mapping.append(_EXT)      
    
    _STR = []
    for node in doc.getElementsByTagName("format"):
        L = node.getElementsByTagName("str")
        for node2 in L:
            str = ""
            for node3 in node2.childNodes:
                if node3.nodeType == Node.TEXT_NODE:
                    str += node3.data
            _STR.append(str)
    print _STR
    mapping.append(_STR)
    return  mapping


#fSRC = getXMLfile("FormatSource.xml")
#print fSRC[0][0]
#print fSRC[0][1]
#print fSRC[1][0]
#print fSRC[1][1]
#print len(fSRC[0])
#print len(fSRC[0][0])


S = '''vvvv
eofmf ki

ok time
tt ytut aa    
2324 
3355
4
eoftr ^543
r
t
t
'''
whereHDR_EOF = S.find('eofmf') 
whereTRL_BEG = S.find('eoftr')         # search for position
print whereHDR_EOF 
print whereTRL_BEG                            

print "------------------"
S =  S[(whereHDR_EOF+5):(whereTRL_BEG)]
print S
print "------------------"




_PRE_HDR_END = 15
#_POS_TLR_END = 0

def _READ_FILE(_INPUT):
    #read file in binary input
    _FILE_IN = open('C:\\Temp\\'+_INPUT,'rb')
    _DATA = _FILE_IN.read()
    #_POS_TLR_END= len(_INPUT)-_OFFSET_BYTES
    _FILE_IN.close()
    return _DATA

       
def _FILE_PRE_HDR_CHK(_INPUT):    
    print 'Checking File Pre-Header...'
    if 'PRE-HEADER' in _INPUT[0:_PRE_HDR_END]:
        print '\tPre-Header: YES'
        return _PRE_HDR_END
    else:
        print '\tPre-Header: NO'
        return 0   

def _FILE_FORMAT_CHECK(_INPUT,OFFSET):
    _FILE_EXT ='txt'    
    #file type and format check
    if "SMTP" in _INPUT:
        _FILE_EXT = "msg"
    if "PK"  in _INPUT[OFFSET:OFFSET+4]:
        _FILE_EXT = "zip"
    if "MZ"  in _INPUT[OFFSET:OFFSET+4]:
        _FILE_EXT = "exe"     
    if "PDF" in _INPUT[OFFSET:OFFSET+4]:
        _FILE_EXT = "pdf" 
    print '\tFILE FORMAT: '+ _FILE_EXT
    return _FILE_EXT      

def _FILE_CONTENT_CHECK(_INPUT):
    _EMBEDDED_CONTENT = []
    print 'Checking embedded content...'
    #embedded content check
    if "DSC" in _INPUT:
        _EMBEDDED_CONTENT.append('\tEMBEDDED: IMAGE')
    if "Script" in _INPUT: 
        _EMBEDDED_CONTENT.append('\tEMBEDDED: SCRIPT')
    if "setup.exe" in _INPUT:
        _EMBEDDED_CONTENT.append('\tEMBEDDED: SETUP.EXE' )     
    if len(_EMBEDDED_CONTENT) == 0:
        print '\tEMBEDDED CONTENT NONE SPECIFIED'
    else:
        i = 0
        while i < len(_EMBEDDED_CONTENT):
            print _EMBEDDED_CONTENT[i]
            i=+1
    return _EMBEDDED_CONTENT
    
def _WRITE_FILE(_INPUT,OFFSET, FORMAT): 
    _OUTPUT_STR = raw_input('Please enter output file name:')
    if OFFSET > 0:
        print 'Skipping PRE-HEADER and writing new file...'
    file('C:\\Temp\\output\\'+_OUTPUT_STR+'.'+FORMAT,'wb').writelines(_INPUT[OFFSET:len(_INPUT)])
    _FILE_CONTENT_CHECK()   
    print 'Creating file in C:\\Temp\\output\\'+_OUTPUT_STR+'.'+FORMAT 
    print 'TOTAL CHARACTERS READ:'+str(len(_INPUT))

#_FILE_PRE_HDR_CHK()

 
#use this to delete selected line index of a file
#FileList = file('C:\\Temp\\test1.msg', 'rb').readlines()
#del FileList[0] #this seem to delete some chunk bytes
#exit = raw_input('type "exit" when done:')    
    
import tkFileDialog
def open_it():
    filename = tkFileDialog.askopenfilename()
    return filename

def get_filename(file):    
    #print filename.split("/")[-1]
    if ":" in file:
        file = file.split("/")[-1]
        if '.' in file:
            file = file.split(".")[0]
            print file
            return file
        else:
            print file
            return file
    else:
        print "No File selected"

def save_as():
    filename = tkFileDialog.asksaveasfilename()
    print filename  # test


from Tkinter import *
from tkMessageBox import *
_root = Tk()

def rootMsg(msg):     
    msg = Label(_root, text=msg)
    msg.pack(expand=NO, fill=BOTH)
    msg.config(relief=SUNKEN, width=40, height=1, bg='beige') 

def mini_Window(root):
    #print "called the callback!"    
    # create a menu     
    menu = Menu(root)
    root.config(menu=menu)        
    filemenu = Menu(menu)
    menu.add_cascade(label="File", menu=filemenu)
    filemenu.add_command(label="Open", command=open_it)
    filemenu.add_separator()
    filemenu.add_command(label="Save As", command=save_as)
    filemenu.add_command(label="Exit", command=exit)    
    mainloop()
    
#mini_Window(_root) 
   
def exit():
    print "..."