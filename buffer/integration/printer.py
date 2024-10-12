import json

class Printer:

    @staticmethod
    def send_message(message: str, printer_socket) -> None:
        printer_socket.send(json.dumps(message).encode("utf-8")[:1024])
