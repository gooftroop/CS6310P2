#!/bin/bash -e
set -o pipefail

# Use this script to generate the final submission tarball
#
# ASSUMPTIONS:
# source code is contained within the src/ directory
# project report is located at report.pdf
# design study is located at designStudy.pdf   
# DVCS system is correct below (git vs hg)
# repo is currently in the state you want captured (archived)

DVCS="git"

if [ "$#" -ne 1 ] ; then
    echo "USAGE: $0 PROJECT_NUM"
    exit 1
fi

PNUM=$1
PTAG=project${PNUM}

# Setup dir
rm -rf $PTAG
mkdir $PTAG

# copy in source
if [ "$DVCS" == "git" ] ; then
    echo "USING GIT"

    # Disallow uncommitted changes
    if ! git diff-files --quiet --
    then
        echo "ERROR: uncommitted changes found in work area.  Check in before proceeding!"
	exit 1
    fi

    # Disallow uncommitted changes
    if ! git diff-index --cached --quiet HEAD --
    then
        echo "ERROR: uncommitted changes found in work area.  Check in before proceeding!"
	exit 1
    fi

    # Archive files and store rev tag
    git archive HEAD | tar -x -C $PTAG
    REV=`git rev-parse HEAD`
    echo $REV > $PTAG/.gitArchiveRevision
    echo "REVISION $REV"
else
    echo "USING MERCURIAL"
    echo "ERROR: NOT IMPLEMENTED"
    # Disallow uncommitted changes
    # Archive files
    exit 1
fi

# Copy in reports
cp report.pdf $PTAG/$PTAG.report.pdf
cp designStudy.pdf $PTAG/$PTAG.study.pdf

# create tgz
echo
echo "CREATING TAR WITH FILES..."
tar -zcvf $PTAG.tar.gz $PTAG/

