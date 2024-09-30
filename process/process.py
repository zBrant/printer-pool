from integration.buffer import Buffer
from utils.helper import Helper
from utils.log_handler import LogHandler
from time import sleep
from datetime import datetime, timezone, timedelta
import random
import sys

MIN_PRIORITY = 0
MAX_PRIORITY = 10

def verify_args(arguments: list) -> list:
    if len(arguments) < 3:
        Helper.print_usage()
        raise SystemExit(1)
    if not (.0 <= float(arguments[2]) <= 1.):
        LogHandler.generic_log('R value must be between 0 and 1', exit_program=True)
    return [arg for arg in arguments]

def is_to_send_message(r_value: float) -> bool:
    return r_value > random.random()

def get_current_time_with_offset() -> str:
    utc_now = datetime.now(timezone.utc)
    custom_offset = timezone(timedelta(hours=-4))
    return str(utc_now.astimezone(custom_offset))

def create_json(document_to_send) -> dict:
    return {
        'timestamp': get_current_time_with_offset(),
        'document': document_to_send,
        'priority': random.randint(MIN_PRIORITY, MAX_PRIORITY)
    }

def main():
    args = sys.argv[1:]
    address, port, r_value = verify_args(args)
    buffer = Buffer(address, int(port), LogHandler())
    while buffer.is_connected():
        input_user = input('enter message: ')
        json_to_send = create_json(input_user)

        if is_to_send_message(float(r_value)):
            buffer.send_message(json_to_send)
        else:
            LogHandler.generic_log('Error: message not sent')
            sleep(0.1) # 1 millisecond

if __name__ == '__main__':
    main()
