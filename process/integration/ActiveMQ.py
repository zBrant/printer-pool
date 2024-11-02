import stomp

class ActiveMQ:
    def __init__(self, address: str, port: int,log_handler):
        self._client = None
        self._log_handler = log_handler
        self._connect(port, address)

    def _connect(self, port, address) -> None:
        try:
            self._client = stomp.Connection(host_and_ports=[(address, port)])
            self._client.connect('admin', 'admin', wait=True)

            self._client.subscribe(destination='/queue/queue-1', id=1, ack='client-individual')

            self._log_handler.generic_log(f'Connected to activeMQ successfully')
        except Exception as e:
            self._log_handler.generic_log(f'ERROR: {e.args[1]} ', exit_program=True)

    def is_connected(self) -> bool:
        if self.is_connected: return True
        self._log_handler.generic_log('Connection to activeMQ closed', exit_program=True)

    def disconnect(self) -> None:
        self._client.disconnect()
        self._log_handler.generic_log('Connection to activeMQ closed', exit_program=True)

    def send_message(self, msg: dict) -> None:
        try:
            if not self._client.is_connected: raise Exception('ActiveMQ is not connected')
            self._client.send(body=str(msg).encode(), destination='/queue/queue-1')
            self._log_handler.generic_log('Message sent to activeMQ')
        except Exception as e:
            self._log_handler.generic_log(f'ERROR: {e.args}')
