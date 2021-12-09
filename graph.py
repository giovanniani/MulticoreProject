#!/usr/bin/env python3
import os
import matplotlib.pyplot as plt


def parse_file(fname):
    """Read in the file and parse the parameters."""
    with open(fname) as fp:
        lines = [line.strip() for line in fp]

    version = lines[0]
    # Parse the times
    parse_time = lambda line: int(line.split('=')[1].strip()[:-1])
    build_time = parse_time(lines[1])
    query_time = parse_time(lines[2])
    total_run_time = parse_time(lines[3])
    # Parse the counts
    parse_count = lambda line: int(line.split(':')[1].strip())
    query_count = parse_count(lines[4])
    core_count = parse_count(lines[5])
    return {
        'version': version,
        'build_time': build_time,
        'query_time': query_time,
        'total_run_time': total_run_time,
        'query_count': query_count,
        'core_count': core_count,
    }


def query_time_vs_version(query_count):
    """Plot query time versus version for query_count queries."""
    fig, ax = plt.subplots()
    versions = list(set(result['version'] for result in results))
    versions.sort()
    qcounts = list(set(result['query_count'] for result in results))
    qcounts.sort()
    cores = set(result['core_count'] for result in results)
    for core_count in cores:
        times = []
        for version in versions:
            version_times = [result['query_time'] for result in results
                             if (result['query_count'] == query_count
                                 and result['core_count'] == core_count
                                 and result['version'] == version)]
            times.append(version_times[0] if version_times else None)
        ax.plot(versions, times, label='{} cores'.format(core_count))
    # Show the labels
    ax.legend()
    ax.set_ylabel('Time (s)')
    ax.set_title('Query Times with {} Queries'.format(query_count))
    # plt.show()
    plt.savefig('figures/QueryTimeVersion_{}.png'.format(query_count))


result_files = [f for f in os.listdir() if f.startswith('Time-')]
results = [parse_file(f) for f in result_files]
print(results)
for qcount in [1000, 10000, 100000, 1000000]:
    query_time_vs_version(qcount)
