from dummy.nfc_reader import NFCReader
class NFCController:
    def __init__(self):
        self.nfc_reader = NFCReader()


    def read_nfc_code(self):
        code = self.nfc_reader.read_code()
        return code
    

