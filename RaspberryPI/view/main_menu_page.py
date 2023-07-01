import tkinter as tk
from tkinter import messagebox

class MainMenuPage(tk.Frame):
    def __init__(self, parent, controller):
        super().__init__(parent)
        self.controller = controller

        self.create_widgets()

    def create_widgets(self):
        self.welcome_label = tk.Label(self, text="Welcome to the Main Menu!")
        self.manage_key_button = tk.Button(self, text="Manage Keys", command=self.showManageKeyPage)
        self.logout_button = tk.Button(self, text="Logout", command=self.logout)
        
        self.welcome_label.pack()
        self.manage_key_button.pack()
        self.logout_button.pack()

    def logout(self):
        # Perform any necessary operations before logging out
        # For example, clearing session data or saving user progress
        self.controller.logout()
        messagebox.showinfo("Logout", "Logged out successfully!")

    def showManageKeyPage(self):
        # Perform any necessary operations before showing the key management page
        # For example, loading key data or initializing key-related functionality
        self.controller.show_manage_key_page()