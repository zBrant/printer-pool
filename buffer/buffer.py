from utils.helper import Helper
from utils.log_handler import LogHandler
from utils.document_handler import DocumentHandler
from integration.printer import Printer
import socket
import sys
import json
import threading

def connect_socket(server_ip: str, port: int) -> socket.socket:
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind((server_ip, int(port)))
        return server
    except Exception as e:
        LogHandler.generic_log(f'ERROR: {e}', exit_program=True)

def verify_args(arguments: list) -> list:
    try:
        if len(arguments) < 3:
            Helper.print_usage()
            raise SystemExit(1)
        if type(int(arguments[2])) is not int: raise Exception()
    except Exception as e:
        LogHandler.generic_log(f'ERROR: M value must be a int', exit_program=True)
    return [arg for arg in arguments]

def close_connection(client_socket: socket.socket, client_address: str) -> None:
    client_socket.close()
    LogHandler.generic_log(f'Connection with client {client_address[0]}:{client_address[1]} closed')

def handle_clients(client_socket: socket.socket, client_address: str, queue_size: int) -> None:
    try:
        request = client_socket.recv(1024)
        json_message = json.loads(request.decode('utf-8'))
        documentHandler = DocumentHandler(queue_size)

        if json_message['message'] == 'get file': Printer.send_message(documentHandler.get_document(), client_socket)
        else: documentHandler.add_to_docs_queue(json_message)

        if json_message['message'] == 'exit': close_connection(client_socket, client_address)
    except Exception as e:
        LogHandler.generic_log(f'ERROR: {e}', exit_program=True)
    finally:
        client_socket.close()

def run_server() -> None:
    server = None
    try:
        server_ip, port, m_value = verify_args(sys.argv[1:])
        server = connect_socket(server_ip, port)
        server.listen(0)
        LogHandler.generic_log(f'Listening on {server_ip}:{port}')

        while True:
            client_socket, client_address = server.accept()
            LogHandler.generic_log(f'Accepted connection from {client_address[0]}:{client_address[1]}')
            threading.Thread(target=handle_clients, args=(client_socket,client_address,m_value)).start()
    finally:
        server.close()

def main():
    run_server()

if __name__ == "__main__":
    main()
