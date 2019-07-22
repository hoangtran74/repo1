class FormatCheckClass(object):
    def CheckFormat(self, offset, input):
        _FILE_EXT ='UNSPECIFIED'  
        
        #file type and format check
        if ("SMTP" in input[offset[0]:]) & (".COM" in input[offset[0]:]):
            _FILE_EXT = "msg"
        if "PK"   in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "zip"
        if "MZ"   in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "exe"     
        if "PDF"  in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "pdf" 
        if "JFIF" in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "jpg"
        if "GIF"  in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "gif"
        if "ID3"  in input[offset[0]:offset[0]+15]:
            _FILE_EXT = "mp3"            
        print '\tFILE FORMAT: '+ _FILE_EXT
        return _FILE_EXT