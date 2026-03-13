from string import Template
import sys
import os
import shutil
import random

# Templates:
# M = rows
# N = cols
# P = cant package
# package_pos_def = a reemplazar con las defs de posiciones
# package_comp = composicion de posiciones (i.e. la lista de LocOfs separados de ||)
# release_def = defs de release
# destination_def = defs de destinations
# destination_list = lista de destinations
# release_list = lista de release

templates = ['$M', '$N', '$P', '$package_pos_def', '$package_comp', 
             '$release_def', '$destination_def', '$destination_list', '$release_list', '$release_checklist']
#obs: no pueden ser substring unas de otras, esto causa problemas

packagePosTemplate = Template("ltl_property LocOf_$packageNr = [](grab[$packageNr] -> At[$xPos][$yPos])")

releasedTemplate = Template("fluent Released$packageNr = <release[$packageNr],Alphabet\{release[$packageNr]}>")

destinationTemplate = Template("""ltl_property Take${packageNr}to$toX$toY = 
    []((Released$packageNr -> At[$toX][$toY]) 
        && ((With[$packageNr] && At[$toX][$toY]) -> (!Moving W release[$packageNr])) 
        && (With[$packageNr] -> (!arrived[$xPos][$yPos])))""")

def buildTemplateSubstitutions(M,N,P,positions,destinations):
    templateSubstitutions = {}

    templateSubstitutions['$M'] = f"{M}"
    templateSubstitutions['$N'] = f"{N}"
    templateSubstitutions['$P'] = f"{P}"

    templateSubstitutions['$package_pos_def'] = "\n".join(
        [packagePosTemplate.substitute(packageNr=i+1, xPos=positions[i][0], yPos=positions[i][1]) 
                              for i in range(P)])
        
    templateSubstitutions['$release_def'] = "\n".join([releasedTemplate.substitute(packageNr=i+1) for i in range(P)])

    templateSubstitutions['$destination_def'] = "\n".join(
        [destinationTemplate.substitute(packageNr=i+1, xPos=positions[i][0], yPos=positions[i][1], toX=destinations[i][0], toY=destinations[i][1])
                               for i in range(P)])

    templateSubstitutions['$package_comp'] = " || ".join([f"LocOf_{i+1}" for i in range(P)])

    templateSubstitutions['$destination_list'] = ",".join([f"Take{i+1}to{destinations[i][0]}{destinations[i][1]}" for i in range(P)])

    templateSubstitutions['$release_list'] = ",".join([f"Released{i+1}" for i in range(P)])

    templateSubstitutions['$release_checklist'] = " && ".join([f"[]<>Released{i+1}" for i in range(P)])

    return templateSubstitutions

def substituteInLine(line, temp, templateSubstitutions):
    return line.replace(temp, templateSubstitutions[temp])

def listOfPositions(P):# random sin repetir
    allPos = [(x,y) for x in range(M) for y in range(N)]
    random.shuffle(allPos)
    
    return allPos[:P+1]

def makeInstance(M,N,P):
    templateFile = open("DeliveryUAVGridWithAssumption_template.lts", 'r')
    outputFile = open(f"output/DeliveryUAVGridWithAssumption_{M}_{N}_{P}.lts", 'w')

    positions = listOfPositions(P) 
    destinations = listOfPositions(P)
    templateSubstitutions = buildTemplateSubstitutions(M,N,P,positions,destinations)

    for line in templateFile:
        outline = line
        for temp in templates:
            if temp in outline:
                outline = substituteInLine(outline, temp, templateSubstitutions)
        
        outputFile.write(outline)

    outputFile.close()
    templateFile.close()

if __name__ == '__main__':
    if len(sys.argv) < 4:
        print("""ERROR, se espera:
            M (rows), N (columns), P (package ammount)
            los topes de cada uno de los parámetros
            """)

    M_max = int(sys.argv[1])
    N_max = int(sys.argv[2])
    P_max = int(sys.argv[3])

    shutil.rmtree("output")
    os.makedirs("output")

    for M in range(1,M_max+1):
        for N in range(1,N_max+1):
            for P in range(1,P_max+1):
                makeInstance(M, N, min(P, M*N))