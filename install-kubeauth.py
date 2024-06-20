#!/usr/bin/python3
import os

def main():
  os.chmod('./run_kubeauth.sh', 0o700)
  os.system("./run_kubeauth.sh")

if __name__ == '__main__':
  main()