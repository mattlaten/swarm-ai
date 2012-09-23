import win32com.client
from win32com.client import constants

null = None
false = 0
true = 1

import sys

'''
seems like this is the start-up method that needs to be
defined for a self-installing plugin for SoftImage|XSI
'''
def XSILoadPlugin (in_reg):
    in_reg.Author = 'Elliott, R. & Laten, M.'
    in_reg.Name = 'Swarm AI Python Plugin'
    in_reg.Major = 1
    in_reg.Minor = 0
    return true;

'''
make sure terrain is in wavefront OBJ format
perhaps load in from file and convert?
'''
def LoadTerrain(filename):
    f = open(filename)
    #convert to OBJ
    f.close()
    #read into XSI


'''
for the moment we will export animation as a file in Java
and read it in as a file in the plugin
'''
def LoadAnimation(filename):
    f = open(filename)
    #read in metadata: prey, predator etc 
    #read in frame by frame and import into XSI
    f.close()
