#!/usr/bin/python3

import sys

def main():
  sys.stderr.write("Client Id: ")
  clientId = input()

  sys.stderr.write("Client Secret: ")
  clientSecret = input()

  os.system("./run_kubeauth.sh -i "+clientId+" -s "+clientSecret)


if __name__ == '__main__':
  main()