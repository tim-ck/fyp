import tkinter as tk
from tkinter import messagebox

class LoginPage(tk.Frame):
    def __init__(self, parent, controller):
        super().__init__(parent)
        self.controller = controller
        self.create_widgets()

    def create_widgets(self):
        self.username_label = tk.Label(self, text="Username:")
        self.username_entry = tk.Entry(self)

        self.password_label = tk.Label(self, text="Password:")
        self.password_entry = tk.Entry(self, show="*")

        self.login_button = tk.Button(self, text="Login", command=self.login)

        self.username_label.pack()
        self.username_entry.pack()
        self.password_label.pack()
        self.password_entry.pack()
        self.login_button.pack()

    def login(self):
        username = self.username_entry.get()
        password = self.password_entry.get()

        if self.controller.login(username, password):
            messagebox.showinfo("Login Successful", "Welcome, " + username + "!")
            self.controller.show_main_menu_page()
        else:
            messagebox.showerror("Login Failed", "Invalid credentials")
