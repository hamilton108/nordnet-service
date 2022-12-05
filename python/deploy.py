#!/usr/bin/python3

import os
import os.path
import glob
from mako.template import Template

MVN_REPO = "/home/rcs/opt/java/mavenlocalrepo/rcstadheim"

HOME = "/home/rcs/opt/java/nordnet-service"

PROJECT_CLJ = "%s/project.clj" % HOME


def latest_dir(java_project):
    project_path = "%s/%s" % (MVN_REPO, java_project)
    items = os.listdir(project_path)
    project_dirs = []
    for item in items:
        full_path = "%s/%s" % (project_path, item)
        if os.path.isdir(full_path) == True:
            project_dirs.append(item)
    project_dirs.sort(reverse=True)
    return "%s/%s" % (project_path, project_dirs[0])


def latest_build(java_project):
    jars = glob.glob("%s/*.jar" % latest_dir(java_project))
    jars.sort(reverse=True)
    lx = len(java_project) + 1
    result = os.path.basename(jars[0])[lx:].split(".jar")
    return result[0]


def generate_project_clj():
    oahu = latest_build("oahu")
    critters = latest_build("critter-repos")
    nordnet = latest_build("nordnet-repos")
    vega = latest_build("vega")
    tpl = Template(filename="%s/python/templates/project.clj.tpl" % HOME)
    result = tpl.render(oahu=oahu, critters=critters,
                        nordnet=nordnet, vega=vega)
    print(result)
    with(open(PROJECT_CLJ, "w")) as f:
        f.write(result)


if __name__ == "__main__":
    generate_project_clj()
