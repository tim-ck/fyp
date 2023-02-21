import tkinter as tk

def main():
    root = tk.Tk()
    root.title('Door Lock')
    root.geometry('700x700')
    # make a menu bar
    menu_bar = tk.Menu(root)
    root.config(menu=menu_bar)
    # make a menu
    file_menu = tk.Menu(menu_bar)
    menu_bar.add_cascade(label='File', menu=file_menu)
    # add menu items
    file_menu.add_command(label='New')
    file_menu.add_command(label='Open')
    file_menu.add_command(label='Save')
    file_menu.add_command(label='Save As')
    file_menu.add_separator()
    file_menu.add_command(label='Exit')
    # make a toolbar
    
    unlock_button = tk.Button(root, text='Unlock', command=hello)
    unlock_button.pack()
    root.mainloop()


def hello():
    print("Hello World")