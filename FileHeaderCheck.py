class HeaderCheckClass(object):
    def CheckHeader(self,input):
        
        hdrEOF = 0
        trlBEG = 0
        offset = []        
        whereHDR_EOF = input.find('eofhr')
        whereTRL_BEG = input.find('eoftr')
        
        print 'Checking File Pre-Header and Post-Trailer...'        
        
        if whereHDR_EOF == -1:
            print '\tPre-Header: NO' 
        else:            
            hdrEOF = whereHDR_EOF+16
            print '\tPre-Header: YES'   
             
        if whereTRL_BEG == -1:            
            print '\tPost-Trailer: NO'   
            trlBEG = len(input)    
        else:            
            trlBEG = whereTRL_BEG
            print '\tPost-Trailer: YES'
                 
        offset.append(hdrEOF)  
        offset.append(trlBEG)  
          
        return offset
        