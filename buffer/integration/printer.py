class Printer:

    @staticmethod
    def send_message(message: str, printer_socket) -> None:
        printer_socket.send(message.encode("utf-8"))
