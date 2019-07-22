class ContentCheckClass(object):
    def _CheckContent(self, _input):
        _content = []
        
        print 'Checking embedded content...'
        #embedded content check
        if "DSC" in _input:
            _content.append('\tEMBEDDED: IMAGE')
        if "Script" in _input: 
            _content.append('\tEMBEDDED: SCRIPT')            
        
        if len(_content) == 0:
            print '\tEMBEDDED: NONE SPECIFIED'
        if len(_content) > 0:
            i = 0
            while i < len(_content):
                print _content[i]
                i=i+1        
        return _content