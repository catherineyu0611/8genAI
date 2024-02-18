import bluetooth

def start_server():
    server_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)

    port = 1  # You can use any available port (1-30 typically)
    server_socket.bind(("", port))
    server_socket.listen(1)

    print("Waiting for Bluetooth connection on port", port)

    client_socket, client_address = server_socket.accept()
    print("Accepted connection from", client_address)

    try:
        while True:
            data = client_socket.recv(1024)
            if not data:
                break
            print("Received data:", data.decode("utf-8"))
            # Incorporate your logic to process the received data here
    except Exception as e:
        print("Error:", str(e))
    finally:
        client_socket.close()
        server_socket.close()

if __name__ == "__main__":
    start_server()