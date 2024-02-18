#Ethan Su
#February 17, 2024

from gpiozero import DistanceSensor
import time
import math
import bluetooth
from signal import signal, SIGTERM, SIGHUP, pause
from rpi_lcd import LCD

ultrasonic = DistanceSensor(echo = 17, trigger = 4)
lcd = LCD()

#Calculates the average distance measurement for 1 second after a water bottle is detected
def average_distance():
    total = 0
    for i in range (10):
        total += ultrasonic.distance

    average = float(total/10)

    return average

def stable_measurements():
    prev1 = 0
    prev2 = 0
    prev3 = 0
    prev4 = 0
    prev5 = 0
    run_count = 0

    while True:
        prev1 = prev2
        prev2 = prev3
        prev3 = prev4
        prev4 = prev5
        prev5 = ultrasonic.distance

        if run_count >= 6:
            mean = (prev1 + prev2 + prev3 + prev4 + prev5)/5
            std_dev = math.sqrt((prev1 - mean)^2 + (prev2 - mean)^2 + (prev3 - mean)^2 + (prev4 - mean)^2 + (prev5 - mean)^2/5)

            if std_dev < 0.02:
                break

        count += 1

    return True

def main():

    #Bluetooth functionality
    server_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    port = 1
    server_socket.bind(("", port))
    server_socket.listen(1)

    #Check if the connection is accepted
    print("Waiting for Bluetooth connection on port", port)
    client_socket, client_address = server_socket.accept()
    print("Accepted connection from", client_address)

    loading_count = 1

    while True:

        if <AI trigger>:
            stable_measurements()
            <start pump>

            lcd.text("Filling...", 1)
            lcd.text(">" * loading_count, 2)

            if loading_count == 16:
                loading_count = 1
            else:
                loading_count += 1

            if ultrasonic.distance <= (33 - bottle_height + 3):
                <stop pump>
