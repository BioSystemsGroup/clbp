# clbp
Chronic Low-Back Pain project prototype

## Toolchain and Execution Workflow
The Java calls out to an R interpreter to evaluate the Component functions. The component functions are specified in the JSON file for each component. Here's what they look like:
```JSON
{"finished": false,
  "id": 0,
  "name": "Environment",
  "updateRate": 0.25,
  "assignments":[
    "disability = (pain_experience + social_context + coping_skills + affective_state)/4",
    "load = (runif(1,0,1) + lifestyle_activity)/2",
    "anatomy = (runif(1,0,1) + genetics)/2",
    "social_context = ifelse((result <- runif(1,-0.25,0.25) + social_context) <= 0, abs(result), result/2)",
    "coping_skills = ifelse((result <- runif(1,-0.25,0.25) + coping_skills) <= 0, abs(result), result/2)",
    "affective_state = ifelse((result <- runif(1,-0.25,0.25) + affective_state) <= 0, abs(result), result/2)"
  ],
  "variables": {
    "age": 0.3290215,
    "bmi": 0.05901736,
    "sex": 0.04548391,
    "smoking": 0.6439261,
    "sleep": 0.09899958,
    "diet": 0.1175193,
    "hygiene": 0.6948755,
    "lifestyle_activity": 0.871428,
    "load": 0.67018,
    "anatomy": 0.2466199,
    "social_context": 0.3359295,
    "coping_skills": 0.06052026,
    "affective_state": 0.2495662,
    "inflammation": 0.9088308,
    "endplate": 0.2019195,
    "disc": 0.08418573,
    "vertebra": 0.5381004,
    "facets": 0.5065615,
    "psm": 0.7645452,
    "si_joint": 0.7046619,
    "pain_perception": 0.3412077,
    "pain_experience": 0.1528822,
    "genetics": 0.807506,
    "personality": 0.8543439,
    "pain_beliefs": 0.3878178,
    "disability": 0.233903
  }
}
```
There are different ways to call out to R. For convenience, we chose to use the [Graal VM](https://www.graalvm.org/), which makes several languages available with minimal sysadmin and special script-related Java hooks. But if we need "double click" functionality or there's some other reason, this is easily changed.

The development all occured under Linux. And everything in this documentation will be written from that context.  However, there are similar steps that can be taken to execute in Windows or Mac OSX. To execute an experiment, the user creates a directory containing the JSON files. A `run.sh` script comes with [the repository](https://github.com/BioSystemsGroup/clbp) clone (using Git and GitHub). After compilation, the user runs the simulation with a command like:
```
./bin/run.sh -pd ../expdir000
```
Where `expdir000` is the directory containing the JSON files and `.` is the root directory of the repository.

Compilation uses [ANT](https://ant.apache.org/) and can be executed from the root directory of the repsitory like:
```
ant compile
```
If Graal VΜ is not the default Java VM, then a command like this can be used:
```
ant -Dplatforms.Graal_JDK_11.home=/usr/local/graalvm-ce-java11-19.3.1
```
Note the `run.sh` file will have to include the Graal VΜ path as well, if Graal isn't the default VM.

The prototype was built using the MASON libraries for its discrete event and agent-based elements. However, this should be largely invisible to the users unless they intend to read the code. The functionality of the prototype is almost completely isolated to the `assignments` equations in the JSON files.

Output consists of comma separated value (CSV) files, one for each Component, a column for each variable, and a row for each time step. E.g.

```
Time, age, bmi, sex, smoking, sleep, diet, hygiene, lifestyle_activity, load, anatomy, social_context, coping_skills, affective_state, inflammation, endplate, disc, vertebra, facets, psm, si_joint, pain_perception, pain_experience, genetics, personality, pain_beliefs, disability
0.0, 0.3290215, 0.05901736, 0.04548391, 0.6439261, 0.09899958, 0.1175193, 0.6948755, 0.871428, 0.6797314961229786, 0.7394187181866467, 0.1379631321483329, 0.07859488430496037, 0.17860942894907145, 0.9088308, 0.393308665, 0.393308665, 0.393308665, 0.393308665, 0.393308665, 0.393308665, 0.524046943, 0.246805915, 0.807506, 0.8543439, 0.3878178, 0.22320546874999997
0.25, 0.3290215, 0.05901736, 0.04548391, 0.6439261, 0.09899958, 0.1175193, 0.6948755, 0.871428, 0.6024708155098706, 0.5972596698351875, 0.1615162198002003, 0.027041499470675884, 0.059047794271434834, 0.9088308, 0.4086899152472362, 0.4086899152472362, 0.4086899152472362, 0.4086899152472362, 0.4086899152472362, 0.4086899152472362, 0.46695468428571435, 0.246805915, 0.807506, 0.8543439, 0.3878178, 0.1604933401005912
...
1000.0, 0.3290215, 0.05901736, 0.04548391, 0.6439261, 0.09899958, 0.1175193, 0.6948755, 0.871428, 0.9009539511542171, 0.5922992438222021, 0.1218616434186333, 0.02813429696909603, 0.09102729587216007, 0.9088308, 0.3808316376944572, 0.3808316376944572, 0.3808316376944572, 0.3808316376944572, 0.3808316376944572, 0.3808316376944572, 0.5524209968872422, 0.19449515081237978, 0.807506, 0.8543439, 0.3878178, 0.10501368929366417
```
And the state of each Component is exported as a JSON file at the end of the experiment.

## Assignments
The `assignments` in the JSON file specify how each variable is modified in each Component. The Java splits these equations at the "=", interprets the left-hand side (LHS) as the output of the component and the right-hand side (RHS) as the inputs. Self-self loops are as simple as placing the LHS variable in the expression on the RHS of an assignment. But the expressions of all the Components, as a whole, will be easier to understand if the outputs of one component are the inputs of another.

The Components are scheduled and executed by MASON in pseud-random order, simulating parallelism. (They can be made parallel if warranted.) This not only leads to the possibility that two executions of the same experiment might result in different outcome, which we mitigate against with a `seed`. But it means if 2 components reference the same variable, what they "see" as input might be different depending on which component executes first. The same is true for outputs.

The assignments written in this prototype are (loosely) based on the Pain Model diagram ...

