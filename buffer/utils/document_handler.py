class DocumentHandler:

    def __init__(self):
        self._queue = None

    @staticmethod
    def get_document() -> str:
        return 'teste file'

    @staticmethod
    def add_to_docs_queue(json_message) -> None:
        pass
