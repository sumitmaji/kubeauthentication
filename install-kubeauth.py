#!/usr/bin/python3

import sys
import os

def main():
  sys.stderr.write("Client Id: ")
  clientId = input()

  sys.stderr.write("Client Secret: ")
  clientSecret = input()

  os.chmod('./run_kubeauth.sh', 0o700)

  os.system("./run_kubeauth.sh -i "+clientId+" -s "+clientSecret)


if __name__ == '__main__':
  main()