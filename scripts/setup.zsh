#!/bin/zsh

sudo apt install python3.5 pip3.5

sudo pip3 install virtualenv virtualenvwrapper

printf "export VIRTUALENVWRAPPER_PYTHON=`which python3`\nsource '/usr/local/bin/virtualenvwrapper.sh'" >> ~/.zshrc

source ~/.zshrc
mkvirtualenv tensorhack
pip install numpy sklearn pandas jupyter notebook tensorflow
pip install -r ../model/requirements.txt

cd ../model
