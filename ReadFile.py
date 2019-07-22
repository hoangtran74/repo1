import tkFileDialog
class FileReadClass(object):    
    def ReadFile(self):        
        FILE_COLLECTION = []        
        filename = tkFileDialog.askopenfilename() 
        if ":" in filename:               
            _FILE_IN = open(filename,'rb')
            _INPUT = _FILE_IN.read()
            _FILE_IN.close()            
            FILE_COLLECTION.append(_INPUT)         
            filename = filename.split("/")[-1]
            if '.' in filename:
                filename = filename.split(".")[0] 
            FILE_COLLECTION.append(filename)
        else:
            FILE_COLLECTION.append("NO FILE SELECTED")
        return FILE_COLLECTION
    