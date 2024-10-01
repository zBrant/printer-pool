class LogHandler:

    @staticmethod
    def generic_log(message: str, exit_program: bool = False) -> None:
        print(f'[LOG] {message}')
        if exit_program: raise SystemExit(1)
