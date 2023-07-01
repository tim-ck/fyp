from model.key_manager import KeyManager

class ManageKeyController:
    def __init__(self):
        self.key_manager = KeyManager()

    def get_keys(self):
        # Retrieve the list of keys from the key manager
        return self.key_manager.get_keys()

    def generate_key(self):
        # Gene  rate a new key using the key manager
        new_key = self.key_manager.generate_key()
        return new_key

    def delete_key(self, key):
        # Delete the specified key using the key manager
        self.key_manager.delete_key(key)
