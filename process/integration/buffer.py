import socket
import json

class Buffer:
    def __init__(self, address: str, port: int, log_handler):
        self._client = None
        self._log_handler = log_handler
        self._is_connected = False
        self._connect(port, address)

    def _connect(self, port, address)-> None:
        try:
            self._client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self._client.connect((address, port))
            self._is_connected = True
        except Exception as e:
            self._log_handler.generic_log(f'ERROR: {e.args[1]} ', exit_program=True)

    def is_connected(self):
        return self._is_connected or self._log_handler.generic_log('Connection to server closed', exit_program=True)

    def disconnect(self) -> None:
        self._client.close()
        self._log_handler.generic_log('Connection to server closed', exit_program=True)

    def send_message(self, msg: dict) -> None:
        try:
            self._client.send(json.dumps(msg).encode("utf-8")[:1024])
            response = self._client.recv(1024)
            if not response: self._is_connected = False
            else: self._log_handler.generic_log('Message sent to server')
        except Exception as e:
            self._log_handler.generic_log(f'ERROR: {e}')
