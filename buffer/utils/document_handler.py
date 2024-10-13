import queue
from queue import PriorityQueue

class DocumentItem:
    def __init__(self, priority, message):
        self.priority = priority
        self.message = message

    def __lt__(self, other):
        return self.priority < other.priority

    def __le__(self, other):
        return self.priority <= other.priority

    def __gt__(self, other):
        return self.priority > other.priority

    def __ge__(self, other):
        return self.priority >= other.priority

class DocumentHandler:

    _queue = None
    _MSG_POSITION_ON_TUPLE = 1
    _lock = None
    _log_handler = None

    def __init__(self, queue_size: int, threading, log_handler):
        self._queue = PriorityQueue(queue_size)
        self._lock = threading.Lock()
        self._log_handler = log_handler

    def get_document(self) -> str:
        with self._lock:
            try:
                document = self._queue.get(timeout=3)
                self._log_handler.generic_log(f'Retrieved document: {document.message}')
                return document.message
            except queue.Empty:
                self._log_handler.generic_log('Queue is empty, no document to retrieve')
                return ''

    def add_to_docs_queue(self, json_message, client_id: int) -> None:
        priority = int(json_message['priority'])
        message =  { key: json_message[key] for key in json_message if key != 'priority' }
        message['clientId'] = client_id
        with self._lock:
            if not self._queue.full():
                doc_item = DocumentItem(priority, message)
                self._queue.put(doc_item)
                self._log_handler.generic_log(f'Added document from client {client_id} with priority {priority}')
            else:
                self._log_handler.generic_log('Queue is full, unable to add new documents')
