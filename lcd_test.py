from signal import signal, SIGTERM, SIGHUP, pause
from rpi_lcd import LCD

import time

lcd = LCD()

lcd.text("Filling...", 1)

while True:
    for i in range(16):
        lcd.text(">" * (i+1), 2)
        time.sleep(0.7)
pause()

lcd.clear()