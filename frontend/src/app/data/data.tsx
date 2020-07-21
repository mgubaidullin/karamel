interface ITopicInfo {
    name: string;
    partitions: number;
    internal: boolean;
}

export { ITopicInfo };

interface NodeInfo {
    id: string;
    host: string;
    port: number;
    rack: string;
}

export { NodeInfo };

interface KEventIn {
     topic: string;
     partition: number;
     offset: number;
     timestamp: string;
     key: string;
     value: string;
}

export {KEventIn};
