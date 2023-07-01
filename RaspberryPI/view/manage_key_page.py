import tkinter as tk
from tkinter import messagebox
from controller.manage_key_controller import ManageKeyController

class ManageKeyPage(tk.Frame):
    def __init__(self, parent, controller):
        super().__init__(parent)
        self.controller = controller

        self.create_widgets()

    def create_widgets(self):
        self.key_listbox = tk.Listbox(self)
        self.refresh_button = tk.Button(self, text="Refresh", command=self.refresh_keys)
        self.generate_button = tk.Button(self, text="Generate Key", command=self.generate_key)
        self.delete_button = tk.Button(self, text="Delete Key", command=self.delete_key)

        self.key_listbox.pack()
        self.refresh_button.pack()
        self.generate_button.pack()
        self.delete_button.pack()

    def refresh_keys(self):
        self.key_listbox.delete(0, tk.END)  # Clear the listbox
        keys = self.controller.get_keys()
        for key in keys:
            self.key_listbox.insert(tk.END, key)

    def generate_key(self):
        key = self.controller.generate_key()
        messagebox.showinfo("Key Generated", "Generated Key: " + key)
        self.refresh_keys()

    def delete_key(self):
        selected_index = self.key_listbox.curselection()
        if selected_index:
            selected_key = self.key_listbox.get(selected_index)
            self.controller.delete_key(selected_key)
            self.refresh_keys()
        else:
            messagebox.showwarning("No Key Selected", "Please select a key to delete.")

