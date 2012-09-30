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
    bDynamic = true
    bDisplayAsSubmenu = false
    in_reg.RegisterMenu( constants.siMenuMainTopLevelID, "Swarm AI", bDisplayAsSubmenu, bDynamic)
    strPluginName = in_reg.Name
    Application.LogMessage(str(strPluginName) + str(" has been loaded."),constants.siVerbose)
    return true

def XSIUnloadPlugin( in_reg ):

	strPluginName = in_reg.Name
	Application.LogMessage(str(strPluginName) + str(" has been unloaded."),constants.siVerbose)

	return true

def SwarmAI_Init(in_ctxt):
	LoadSimulation('D:\\Dev\\swarm-ai\\plugins\\animation')
	pass


'''
make sure terrain is in wavefront OBJ format
perhaps load in from file and convert?
'''
def LoadTerrain(filename):
    f = open(filename)
    #convert to OBJ
    f.close()
    #use usual loading in of OBJ

'''
for the moment we will export animation as a file in Java
and read it in as a file in the plugin
'''
def LoadSimulation(filename):
    f = open(filename)
    animap = {}
    #e1 x y z i j k n
    lines = f.readlines()
    #LoadTerrain(lines[0])
    for name in lines[1].split():
        animap[name] = {'x':[], 'y':[], 'z':[], 'i':[], 'j':[], 'k':[]}
        animap[name]['obj'] = Application.ActiveSceneRoot.addGeometry('Sphere','NurbsSurface')
	#Application.LogMessage(animap[name]['x'])
	for line in lines[2:]:
		if line == '':
			break
		e = line.split()
		#Application.LogMessage(animap[e[0]])
		#animap[e[0]]['x'].extend([e[-1],e[1]])
		#animap[e[0]]['x'].extend([e[-1],e[1]])
		#animap[e[0]]['y'].extend([e[-1],e[2]])
		#animap[e[0]]['z'].extend([e[-1],e[3]])
		#animap[e[0]]['i'].extend([e[-1],e[4]])
		#animap[e[0]]['j'].extend([e[-1],e[5]])
		#animap[e[0]]['k'].extend([e[-1],e[6]])
	for key in animap.keys():
		animap[key]['obj'].PosX.addFCurve2(animap[key]['x'])
		animap[key]['obj'].PosY.addFCurve2(animap[key]['y'])
		animap[key]['obj'].PosZ.addFCurve2(animap[key]['z'])
    #read in first frame, 
    #read in metadata: prey, predator etc 
    #read in frame by frame and import into XSI
    f.close()
