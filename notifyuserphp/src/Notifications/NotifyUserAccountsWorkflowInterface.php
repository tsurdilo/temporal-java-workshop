<?php

declare(strict_types=1);

namespace Temporal\Workshop\Notifications;

use Temporal\Workflow\QueryMethod;
use Temporal\Workflow\WorkflowInterface;
use Temporal\Workflow\WorkflowMethod;

#[WorkflowInterface]
interface NotifyUserAccountsWorkflowInterface
{
    #[WorkflowMethod(name: "NotifyUserAccounts")]
    public function notify(
        array $accountIds
    );

    #[QueryMethod]
    public function getCount(): int;
}