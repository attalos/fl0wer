to limit cpu number run:
```
taskset -c 0,2 [cmd]
taskset -c 0,2 ./execute TASKFILE JARFILE [-r]
```

to see which cores are different physical cores (and not only hyperthreats) use
```
cat /proc/cpuinfo
```
and look for the `core id`
