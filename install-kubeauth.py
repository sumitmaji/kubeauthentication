#!/usr/bin/python3

import sys
import os

def main():
  sys.stderr.write("Client Id: ")
  clientId = input()

  sys.stderr.write("Client Secret: ")
  clientSecret = input()

  sys.stderr.write("Docker User: ")
  dockerUser = input()

  sys.stderr.write("Docker Password: ")
  dockerPwd = input()

  os.chmod('./run_kubeauth.sh', 0o700)

  os.system("./run_kubeauth.sh -i "+clientId+" -s "+clientSecret+" -u "+dockerUser+" -p "+dockerPwd+" -a "+"keycloak"+" -r "+"GokDevelopers")


if __name__ == '__main__':
  main()