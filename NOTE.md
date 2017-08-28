# The source structure

The project at the moment consists of 2 modules:
+ The android app itself made with Kotlin
+ The Tensorflow project to generate and train a model for letter recognition

In order to minimize friction when it comes to initialize the project, and since the project is quite small, it is reasonable to refactor it into a monorepo structure.

We will have 2 packages, one per module. The naming of these module however, can follow two convention. Either we name them based on their technology, e.g android-app and tensorflow-[...] <- the last bit would be quite challenging to name. We can name it "model-maker", "project", etc... but it will not feel right. Furthermore, this highlight the technology, but not the purpose of the module.

If what we seek is the finalization of the module, then the name of each module should emphasize its purpose, not the technology. As we must use any technology available to achieve the goal.

The second convention seems to fit this line of reasoning. We will name the first, `launcher`, the second `model`.

---
# Setup Bazel and Tensorflow optimization for some TF tools

```
mkdir ~/src
cd ~/src
wget https://github.com/bazelbuild/bazel/releases/download/0.5.3/bazel-0.5.3-dist.zip

unzip bazel-0.5.3-dist.zip -d bazel
cd bazel
./compile.sh

# Open new terminal
cd ~/src
gcl git@github.com:tensorflow/tensorflow.git
cd tensorflow
./configure

# WHEN bazel compile is done, switch back to that terminal
mv output/bazel ~/bin/bazel

# Add ~/bin to your PATH if you haven't.
chmod +x ~/bin/bazel

# Test bazel:
bazel version

# Switch to tensorflow terminal

bazel build tensorflow/tools/graph_transforms:transform_graph
bazel build tensorflow/tools/graph_transforms:summarize_graph
bazel build tensorflow/contrib/util:convert_graphdef_memmapped_format
```

The tools are built. Now we just need to use them on the `graph.pb` to transform it accordingly:


```
# Go to the model module
cd /path/to/janusLauncher/model

# Use summarize_graph tool to optimize the graph size. The graph should shrink by ~0.1MB

~/src/tensorflow/bazel-bin/tensorflow/tools/graph_transforms/summarize_graph --in_graph=graph.pb

~/src/tensorflow/bazel-bin/tensorflow/tools/graph_transforms/transform_graph \
--in_graph=graph.pb \
--out_graph=opt_graph.pb \
--inputs='input_1' \
--outputs='Softmax' \
--transforms='strip_unused_nodes(type=float, shape="1,299,299,3") remove_nodes(op=Identity, op=CheckNumerics) round_weights(num_steps=256) fold_constants(ignore_errors=true) fold_batch_norms fold_old_batch_norms'

~/src/tensorflow/bazel-bin/tensorflow/tools/graph_transforms/transform_graph \
--in_graph=opt_graph.pb \
--out_graph=shrink_graph.pb \
--inputs='input_1' \
--outputs='Softmax' \
--transforms='quantize_weights strip_unused_nodes'

~/src/tensorflow/bazel-bin/tensorflow/contrib/util/convert_graphdef_memmapped_format --in_graph=opt_graph.pb --out_graph=mem_graph.pb

```

---
# transform_graph

Seems like the inputs and outpus are wrong....

```
2017-08-19 13:09:41.584398: I tensorflow/tools/graph_transforms/transform_graph.cc:264] Applying strip_unused_nodes

2017-08-19 13:09:41.603677: E tensorflow/tools/graph_transforms/transform_graph.cc:210] Input node Softmax not found in graph

2017-08-19 13:09:41.604259: E tensorflow/tools/graph_transforms/transform_graph.cc:211] usage: /home/jojo/src/tensorflow/bazel-bin/tensorflow/tools/graph_transforms/transform_graph
```

The 2nd line seems to indicate that all input need to match somehow

---
# Activities

1. Main activity: Has drawing pad, and a partial list of things. Maybe for first the list will from user marking for favorite
2. List activity: Has the full list


---
# Setup EMNIST data to be used with the training scripts

Since my hard-drive is limited in space, I had to store EMNIST on an external drive. After which, I would do a symbolic link. There're a couple of quirk. Here's the command that was giving me trouble:

```
cd path/to/matlab-parent
ln -s matlab/ ~/pro/janusLauncher/model/data
```

The symlink was created successfully, but I cannot get inside through the symlink. Searching around, and found this hilarious thread: [link](http://www.linuxquestions.org/questions/linux-newbie-8/invalid-cross-device-link-731268/). However, it wasn't very related.

Then I realized that the path was wrong. In order to make a soft symlink, you must use absolute path. Here's the working command:

```
ln -s $PWD/matlab/ ~/pro/janusLauncher/model/data
```

That is, if you cloned this project into `~/pro`

To those who have spaces in their hard-drive, the setup script provided should download the dataset automatically for you. Feel free to port it over to other env.

---
# Setup an OK python hacking environment for Machine Learning

1. Install `python3.5` and `pip3.5`, use them, set them default.

2. Install `virtual-env` and `virtualenvwrapper`
	- `sudo pip3 install virtualenv virtualenvwrapper`

3. Setup `virtualenvwrapper` and create a new environment called `tensorhack`:
	- Add the code below to your shell startup:

```sh
	export WORKON_HOME=$HOME/.virtualenvs
	source /usr/local/bin/virtualenvwrapper.sh
```
	- Refresh shell config: `source ~/.zshrc`
	- `mkvirtualenv tensorhack`
	- `pip install numpy sklearn pandas jupyter notebook tensorflow scipy`

4. Learn the following `pip` command:
	- `pip freeze --local > requirements.txt`
	- `pip install -r requirements.txt`

5. Whenever you see a python Tensorflow project, use `workon tensorhack` and you are mostly set.

6. For convenience, I created a script to run all of this for Debian distro running `zsh` in `./scripts/setup.zsh`. `chmod +x` it and run. Feel free to port it to other env/shell.

---

# Using delegate to lazy load things
2017-08-05T06:24:12.300Z

Working with Android suffers from ancient artifact such as manually handling delegate/promise/asynchronous/background process. To see Kotlin can actually handle this case is a great sign.

Prior in Java, one would have to cache the result of a background call, then check for the cached result in subsequence render.

In Kotlin, the same mechanism is almost impossible, since it does not allow lateinit field that is nullable. However, using a delegate, the cache could be loaded inside the delegate lambda itself. Thus, inside the background loader callback, one can simply return the delegate.

Checkout AppsLoader for a demonstration.

[Kotlin Delegated Properties](https://kotlinlang.org/docs/reference/delegated-properties.html)

---

# Android SDK update quirk on system low on tmp

My laptop was running low on storage when attempting to upgrade android SDK. Indeed the partition used for tmp was only 1.5GB freed. The solution to this was quite simple. I simply create a tmp directory in another partition, or even external storage drive. Then simply remove the `/tmp` directory with root, then symlink the new `/tmp` like so:

```
sudo rm /tmp
sudo ln -s /path/to/another/tmp /tmp
sudo chmod 777 /tmp
```

This allows Android SDK to upgrade gracefully. However, upon reboot, the `/tmp` directory wasn't mounted correctly and thus the vmlinuz image cannot populate properly, my XServer simply crashed. This was due to the fact that I was using an external drive for my tmp, a silly act. Thus, I had to go to ty1, remove the symlink, and create a new /tmp again. Reboot and things worked:

```
CTRL+ALT+F1 -> login

sudo rm /tmp
sudo mkdir /tmp
sudo chmod 777 /tmp
sudo reboot

```

# Consuming TF model in Android

```sh
cd path/to/janusLauncher
# Make sure you train the model and generate the graph
cp model/graph.pb launcher/app/src/main/assets
```

# Generating the label file

Regarding tutorial online that one can duck on the subject of "Tensorflow" and "Andoid/IOS", there are two sets of tutorial. The first dive deeply into acquiring the dataset, creating the script to train a model, and stop at testing the model and gathering the best confidence. The second goes through the pipeline of integrating a fully-trained model into an app, which goes from acquiring a pre-trained model, then implement the Tensorflow API on the mobile platform, then showcasing the result.

There is a gap between these two tutorial. The gap is hidden under the technical and thus is very easy for both segment of the tutorial author to miss. That is, the produced model of the first, is not compatible with the model used at the start of the second.

There are several tutorial, amongst them by stratospark, which looked into converting the finalized model into a graph object using TF. This is great. However, when it comes to classifying problem like EMNIST, there need a label file. We call it `label.txt`

Ducking for `label.txt` yield some example:

https://github.com/ageron/handson-ml/blob/master/datasets/inception/imagenet_class_names.txt
https://github.com/mari-linhares/mnist-android-tensorflow/blob/master/MnistAndroid/app/src/main/assets/labels.txt

It seems to be just a text file of all the available label. Which makes one wonder if that is all is needed, a text file of all label. The question is, in what order should these label lay? Is there a format, or just put them in? Is there a need to preprocess the label?

Turned out, the consuming code from the Android side does shed some light on this matter. The label file if found, will be read line by line by a BufferedReader. For each line, it simply add it to the label _Vector_. The use o the vector class seems to indicate that the order does not matter, and that we should be able to create the label file for our model in a very straight forward manner.

# Finding the input node and output node name of a TF model

This is quite tricky. After several trial and realization, the steps are as follow:
First when constructing the model, name the input and output so that it is easier to identify them. In janus, input are named `main_input` and output `main_output`

Now, after the model has been trained, the input name will stays intact, but the output name will be modified, and you will have to search for it. Look at the very last operator of the model. For janus, it is

```
output = kl.Dense(nb_classes, activation='softmax', name='main_output')(hidden)
```

The operator is Dense, but the activation function is softmax. In order to use the `transform_graph` program with the model, we need to find the name of this node. If you outputed the graph.pbtxt you are in luck. Since it is just a giant json file, grepping it for the output node is doable. However, doing so will only give you instances of the `main_output` string, but not the name of the node.

In order to find node name, simply `grep name` like so:
```sh
cd model/freeze
cat graph.pbtxt | grep name
```

The output will be a list of node name. Now you will just need to find a node with a name related to main_output with softmax. For Janus, it is indeed `main_output/Softmax`
