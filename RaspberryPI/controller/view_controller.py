from model.key_manager import KeyManager
from view.main_menu_page import MainMenuPage
from view.manage_key_page import ManageKeyPage
from view.login_page import LoginPage

class ViewController:
    def __init__(self, app):
        self.key_manager = KeyManager()
        self.app = app

    def login(self, username, password):
        return self.key_manager.verify_credentials(username, password)

    def logout(self):
        # Perform any necessary cleanup or logout-related actions here
        pass

    def show_login_page(self):
        self.login_frame = LoginPage(self.app.root, self)
        #show frame in center of the screen
        self.login_frame.place(relx=0.5, rely=0.5, anchor="center")
        self.login_frame.pack()

    def show_main_menu_page(self):
        # Perform any necessary operations before showing the main menu page
        # For example, initializing data or fetching user-specific information
        self.login_frame.destroy()
        self.main_menu_frame = MainMenuPage(self.app.root, self)
        self.main_menu_frame.pack()

    def show_manage_key_page(self):
        # Perform any necessary operations before showing the key management page
        # For example, loading key data or initializing key-related functionality
        self.main_menu_frame.destroy()
        self.manage_key_frame = ManageKeyPage(self.app.root, self)
        self.manage_key_frame.pack()