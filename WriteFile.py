class WriteFileClass(object):   
    def FileWrite(self, _offset, _input, _format):
        
        if _offset > 0:            
            print 'Writing new file...'
        file('C:\\Temp\\output\\'+
             _input[1]+'.'+_format,'wb').writelines(_input[0][_offset[0]:_offset[1]])
        print 'Creating file in C:\\Temp\\output\\'+_input[1]+'.'+_format 
        print 'TOTAL CHARACTERS READ:'+str(len(_input[0]))
     
        raw_input("Press ENTER to Exit:")   