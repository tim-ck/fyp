import tkinter as tk
import time
import threading
from controller.view_controller import ViewController
from controller.nfc_reader_controller import NFCController
from controller.authentication_controller
class MyApp:
    def __init__(self, root):
        self.root = root
        self.controller = ViewController(self)
        self.controller.show_login_page()

def keyReader_thread():
    while True:
        nfcController = NFCController()
        code = nfcController.read_nfc_code()
        if code != None:
            
        time.sleep(5)

try:
    # create main thread for window
    keyReader_thread = threading.Thread(target=keyReader_thread)
    keyReader_thread.start()
except:
    print("e")

root = tk.Tk()
# Set the window size
window_width = 400
window_height = 300
screen_width = root.winfo_screenwidth()
screen_height = root.winfo_screenheight()
x = (screen_width - window_width) // 2
y = (screen_height - window_height) // 2
# Set the window position
root.geometry(f"{window_width}x{window_height}+{x}+{y}")
app = MyApp(root)
root.mainloop()