from queue import PriorityQueue

class DocumentHandler:

    _queue = None
    _MSG_POSITION_ON_TUPLE = 1

    def __init__(self, queue_size: int):
        self._queue = PriorityQueue(queue_size)

    def get_document(self) -> str:
        return self._queue.get()[self._MSG_POSITION_ON_TUPLE]

    def add_to_docs_queue(self, json_message) -> None:
        priority = (json_message['priority'])
        message =  { key: json_message[key] for key in json_message if key != 'priority' }
        self._queue.put(( priority, message ))
