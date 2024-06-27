graph LR

    subgraph client
    direction LR
    phone1[Phone 1]
    phone2[Phone 2]
    phone3[Phone N]
    end

    subgraph api[Api]
    direction LR
    nginx
    nodered1[NodeRed 1]
    nodered2[NodeRed 2]
    nodered3[NodeRed N]
    end

    subgraph persistence[Persistence]
    direction LR
    postgres[PostgreSQL]
    thingworx[ThingWorx]
    end

    subgraph redis[Redis Cluster]
    direction TB
    redis1[Redis 1]
    redis2[Redis 2]
    redis3[Redis N]
    end

    subgraph background[Background Services]
    direction LR
    noderedb[NodeRed Redis]
    noderedplots[NodeRed Plots]
    noderedfirebase[NodeRed Firebase]
    end

    subgraph edge[Edge]
    direction LR
    arduino1[Arduino 1]
    arduino2[Arduino 2]
    arduino3[Arduino N]
    end

    subgraph firebase[Firebase]
    direction LR
    fcm[Firebase Cloud Messaging]
    end

    client -- "REST" --> api
    client <-- "Google Play Services" --> fcm
    nginx --> nodered1
    nginx --> nodered2
    nginx --> nodered3
    api <--> persistence
    api <--> redis
    redis <--> noderedb
    persistence --> background
    edge -- "MQTT" --> thingworx
    edge -- "MQTT" --> api
    noderedfirebase --> fcm
    client -- "Wifi pairing/REST" --> edge

