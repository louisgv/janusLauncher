#!/bin/zsh

sudo apt install python3

sudo pip3 install virtualenv virtualenvwrapper

printf "export VIRTUALENVWRAPPER_PYTHON=`which python3`\nsource '/usr/local/bin/virtualenvwrapper.sh'" >> ~/.zshrc

source ~/.zshrc
mkvirtualenv tensorhack
pip install numpy sklearn pandas jupyter notebook tensorflow
pip install -r ../model/requirements.txt

cd ../model

curl https://cloudstor.aarnet.edu.au/plus/index.php/s/7YXcasTXp727EqB/download -o data.zip

unzip data.zip
rm data.zip
mv matlab data
