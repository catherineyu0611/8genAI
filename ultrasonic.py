from gpiozero import DistanceSensor
import time

ultrasonic = DistanceSensor(echo = 17, trigger = 4)

while True:
    print(ultrasonic.distance)
    time.sleep(0.2)